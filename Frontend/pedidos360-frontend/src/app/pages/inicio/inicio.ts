import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MsalService } from '@azure/msal-angular';

@Component({
  imports: [],
  selector: 'app-inicio',
  styleUrl: './inicio.css',
  templateUrl: './inicio.html',
})
export class Inicio {

  constructor(
    private authService: MsalService,
    private router: Router
  ) {}

  iniciarSesion(): void {
    this.authService.loginPopup({
      scopes: ['openid', 'profile']
    }).subscribe({
      next: (resultado) => {
        this.authService.instance.setActiveAccount(resultado.account);

        console.log('Inicio de sesión correcto');
        console.log('Usuario:', resultado.account);

        this.router.navigate(['/pedidos']);
      },
      error: (error) => {
        console.error('Error al iniciar sesión:', error);
      }
    });
  }
}