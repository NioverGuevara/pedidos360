
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface InformacionCola {
  nombre: string;
  existe: boolean;
  mensajes: number;
  consumidores: number;
}

export interface EstadoColas {
  'pedidos.queue': InformacionCola;
  'pedidos.dlq': InformacionCola;

  'notificaciones.queue': InformacionCola;
  'notificaciones.dlq': InformacionCola;

  'tickets.queue': InformacionCola;
  'tickets.dlq': InformacionCola;
}

@Injectable({
  providedIn: 'root'
})
export class RabbitmqService {

  // API del microservicio administrativo RabbitMQ
  private apiUrl = 'https://pedidos360-ng-aws26.duckdns.org/api/rabbitmq';

  constructor(private http: HttpClient) {}

  // Obtener el estado de las seis colas
  obtenerColas(): Observable<EstadoColas> {
    return this.http.get<EstadoColas>(
      `${this.apiUrl}/colas`
    );
  }
}
