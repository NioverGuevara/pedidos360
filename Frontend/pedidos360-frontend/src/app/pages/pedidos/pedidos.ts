import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MsalService } from '@azure/msal-angular';

import { Pedido } from '../../models/pedido';
import { PedidoService } from '../../services/pedido';

@Component({
  selector: 'app-pedidos',
  imports: [CommonModule, FormsModule],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.css'
})
export class Pedidos implements OnInit {

  pedidos: Pedido[] = [];

  nombreUsuario = '';
  correoUsuario = '';

  mostrarFormulario = false;
  modoEdicion = false;
  pedidoEditandoId?: number;

  nuevoPedido: Pedido = {
    cliente: '',
    producto: '',
    cantidad: 1,
    estado: 'PENDIENTE'
  };

  constructor(
    private pedidoService: PedidoService,
    private msalService: MsalService
  ) {}

  ngOnInit(): void {
    this.obtenerUsuario();
    this.obtenerAccessToken();
    this.cargarPedidos();
  }

  obtenerUsuario(): void {
    const cuenta =
      this.msalService.instance.getActiveAccount() ??
      this.msalService.instance.getAllAccounts()[0];

    if (cuenta) {
      this.msalService.instance.setActiveAccount(cuenta);

      this.nombreUsuario = cuenta.name ?? 'Usuario';
      this.correoUsuario = cuenta.username;

      console.log('Usuario autenticado:', cuenta);
      console.log('Claims del token:', cuenta.idTokenClaims);
    }
  }

  obtenerAccessToken(): void {
    const cuenta =
      this.msalService.instance.getActiveAccount() ??
      this.msalService.instance.getAllAccounts()[0];

    if (!cuenta) {
      console.error('No hay una cuenta autenticada.');
      return;
    }

    this.msalService.instance.acquireTokenSilent({
      account: cuenta,
      scopes: [
        'api://bb47a6a0-1b96-43fb-b107-1576638ee9e8/Pedidos.ReadWrite'
      ]
    })
    .then((respuesta) => {
      console.log('Access token obtenido correctamente');
      console.log('Scopes concedidos:', respuesta.scopes);
      console.log('Expira:', respuesta.expiresOn);
    })
    .catch((error) => {
      console.error('Error al obtener access token:', error);
    });
  }

  cargarPedidos(): void {
    this.pedidoService.obtenerPedidos().subscribe({
      next: (datos) => {
        this.pedidos = datos;
      },
      error: (error) => {
        console.error('Error al obtener los pedidos:', error);
      }
    });
  }

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

  guardarPedido(): void {
    if (
      !this.nuevoPedido.cliente.trim() ||
      !this.nuevoPedido.producto.trim() ||
      this.nuevoPedido.cantidad <= 0
    ) {
      alert('Completa correctamente todos los campos.');
      return;
    }

    if (this.modoEdicion && this.pedidoEditandoId !== undefined) {
      this.pedidoService
        .actualizarPedido(this.pedidoEditandoId, this.nuevoPedido)
        .subscribe({
          next: () => {
            this.cerrarFormulario();
            this.cargarPedidos();
          },
          error: (error) => {
            console.error('Error al actualizar el pedido:', error);
            alert('No fue posible actualizar el pedido.');
          }
        });
    } else {
      this.pedidoService.crearPedido(this.nuevoPedido).subscribe({
        next: () => {
          this.cerrarFormulario();
          this.cargarPedidos();
        },
        error: (error) => {
          console.error('Error al crear el pedido:', error);
          alert('No fue posible crear el pedido.');
        }
      });
    }
  }

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
        console.error('Error al eliminar el pedido:', error);
        alert('No fue posible eliminar el pedido.');
      }
    });
  }

  cerrarSesion(): void {
    this.msalService.logoutRedirect({
      postLogoutRedirectUri: 'http://localhost:4200'
    });
  }
}