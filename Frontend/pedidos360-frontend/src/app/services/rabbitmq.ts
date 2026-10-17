
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
}

@Injectable({
  providedIn: 'root'
})
export class RabbitmqService {

  // Segundo microservicio: RabbitMQ Admin
  private apiUrl = 'http://localhost:8081/api/rabbitmq';

  constructor(private http: HttpClient) {}

  // Consultar estado de las colas
  obtenerColas(): Observable<EstadoColas> {
    return this.http.get<EstadoColas>(
      `${this.apiUrl}/colas`
    );
  }
}
