
import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MsalService } from '@azure/msal-angular';

import { Pedido } from '../../models/pedido';
import { PedidoService } from '../../services/pedido';

import {
  RabbitmqService,
  InformacionCola
} from '../../services/rabbitmq';

@Component({
  selector: 'app-pedidos',
  imports: [CommonModule, FormsModule],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.css'
})
export class Pedidos implements OnInit {

  // PEDIDOS
  pedidos: Pedido[] = [];

  // USUARIO AUTENTICADO
  nombreUsuario = '';
  correoUsuario = '';

  // FORMULARIO
  mostrarFormulario = false;
  modoEdicion = false;
  pedidoEditandoId?: number;

  // RABBITMQ
  colasRabbitmq: InformacionCola[] = [];
  cargandoColas = false;
  errorColas = '';

  nuevoPedido: Pedido = {
    cliente: '',
    producto: '',
    cantidad: 1,
    estado: 'PENDIENTE'
  };

  constructor(
    private pedidoService: PedidoService,
    private msalService: MsalService,
    private rabbitmqService: RabbitmqService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.obtenerUsuario();
    this.obtenerAccessToken();
    this.cargarPedidos();
    this.cargarColas();
  }

  // OBTENER INFORMACION DEL USUARIO
  obtenerUsuario(): void {
    const cuenta =
      this.msalService.instance.getActiveAccount() ??
      this.msalService.instance.getAllAccounts()[0];

    if (cuenta) {
      this.msalService.instance.setActiveAccount(cuenta);

      this.nombreUsuario = cuenta.name ?? 'Usuario';
      this.correoUsuario = cuenta.username;

      console.log(
        'Usuario autenticado:',
        cuenta.username
      );
    }
  }

  // OBTENER TOKEN JWT DE MICROSOFT
  obtenerAccessToken(): void {
    const cuenta =
      this.msalService.instance.getActiveAccount() ??
      this.msalService.instance.getAllAccounts()[0];

    if (!cuenta) {
      console.error(
        'No hay una cuenta autenticada.'
      );
      return;
    }

    this.msalService.instance.acquireTokenSilent({
      account: cuenta,
      scopes: [
        'api://bb47a6a0-1b96-43fb-b107-1576638ee9e8/Pedidos.ReadWrite'
      ]
    })
    .then((respuesta) => {
      console.log(
        'Access token obtenido correctamente'
      );

      console.log(
        'Scopes concedidos:',
        respuesta.scopes
      );

      console.log(
        'Expira:',
        respuesta.expiresOn
      );
    })
    .catch((error) => {
      console.error(
        'Error al obtener access token:',
        error
      );
    });
  }

  // CONSULTAR PEDIDOS
  cargarPedidos(): void {
    this.pedidoService.obtenerPedidos().subscribe({
      next: (datos) => {
        this.pedidos = datos;
        this.cdr.detectChanges();
      },

      error: (error) => {
        console.error(
          'Error al obtener los pedidos:',
          error
        );

        this.cdr.detectChanges();
      }
    });
  }

  // CONSULTAR LAS SEIS COLAS DE RABBITMQ
  cargarColas(): void {
    this.cargandoColas = true;
    this.errorColas = '';

    this.cdr.detectChanges();

    this.rabbitmqService.obtenerColas().subscribe({
      next: (datos) => {
        console.log(
          'Respuesta recibida de RabbitMQ:',
          datos
        );

        this.colasRabbitmq = [
          datos['pedidos.queue'],
          datos['notificaciones.queue'],
          datos['tickets.queue'],
          datos['pedidos.dlq'],
          datos['notificaciones.dlq'],
          datos['tickets.dlq']
        ].filter(
          (cola): cola is InformacionCola => !!cola
        );

        this.cargandoColas = false;
        this.errorColas = '';

        this.cdr.detectChanges();

        console.log(
          'Colas cargadas correctamente:',
          this.colasRabbitmq
        );
      },

      error: (error) => {
        console.error(
          'Error al consultar RabbitMQ:',
          error
        );

        this.errorColas =
          'No fue posible consultar las colas de RabbitMQ.';

        this.cargandoColas = false;
        this.cdr.detectChanges();
      }
    });
  }

  // ABRIR FORMULARIO PARA CREAR PEDIDO
  abrirFormulario(): void {
    this.modoEdicion = false;
    this.pedidoEditandoId = undefined;

    this.nuevoPedido = {
      cliente: '',
      producto: '',
      cantidad: 1,
      estado: 'PENDIENTE'
    };

    this.mostrarFormulario = true;
  }

  // ABRIR FORMULARIO PARA EDITAR PEDIDO
  editarPedido(pedido: Pedido): void {
    if (pedido.id === undefined) {
      return;
    }

    this.modoEdicion = true;
    this.pedidoEditandoId = pedido.id;

    this.nuevoPedido = {
      id: pedido.id,
      cliente: pedido.cliente,
      producto: pedido.producto,
      cantidad: pedido.cantidad,
      estado: pedido.estado
    };

    this.mostrarFormulario = true;
  }

  // CERRAR FORMULARIO
  cerrarFormulario(): void {
    this.mostrarFormulario = false;
    this.modoEdicion = false;
    this.pedidoEditandoId = undefined;

    this.nuevoPedido = {
      cliente: '',
      producto: '',
      cantidad: 1,
      estado: 'PENDIENTE'
    };
  }

  // CREAR O ACTUALIZAR PEDIDO
  guardarPedido(): void {
    if (
      !this.nuevoPedido.cliente.trim() ||
      !this.nuevoPedido.producto.trim() ||
      this.nuevoPedido.cantidad <= 0
    ) {
      alert(
        'Completa correctamente todos los campos.'
      );
      return;
    }

    if (
      this.modoEdicion &&
      this.pedidoEditandoId !== undefined
    ) {
      this.pedidoService
        .actualizarPedido(
          this.pedidoEditandoId,
          this.nuevoPedido
        )
        .subscribe({
          next: () => {
            this.cerrarFormulario();
            this.cargarPedidos();
          },

          error: (error) => {
            console.error(
              'Error al actualizar el pedido:',
              error
            );

            alert(
              'No fue posible actualizar el pedido.'
            );
          }
        });

    } else {
      this.pedidoService
        .crearPedido(this.nuevoPedido)
        .subscribe({
          next: () => {
            this.cerrarFormulario();
            this.cargarPedidos();
            this.cargarColas();
          },

          error: (error) => {
            console.error(
              'Error al crear el pedido:',
              error
            );

            alert(
              'No fue posible crear el pedido.'
            );
          }
        });
    }
  }

  // ELIMINAR PEDIDO
  eliminarPedido(pedido: Pedido): void {
    if (pedido.id === undefined) {
      return;
    }

    const confirmar = confirm(
      `¿Seguro que deseas eliminar el pedido #${pedido.id}?`
    );

    if (!confirmar) {
      return;
    }

    this.pedidoService.eliminarPedido(pedido.id).subscribe({
      next: () => {
        this.cargarPedidos();
      },

      error: (error) => {
        console.error(
          'Error al eliminar el pedido:',
          error
        );

        alert(
          'No fue posible eliminar el pedido.'
        );
      }
    });
  }

  // CERRAR SESION DE MICROSOFT
  cerrarSesion(): void {
    this.msalService.logoutRedirect({
      postLogoutRedirectUri:
        'http://localhost:4200'
    });
  }
}
