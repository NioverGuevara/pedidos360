import { Component, OnInit } from '@angular/core';
import { broadcastResponseToMainFrame } from '@azure/msal-browser/redirect-bridge';

@Component({
  selector: 'app-redirect',
  standalone: true,
  template: '<p>Procesando inicio de sesión...</p>'
})
export class Redirect implements OnInit {

  ngOnInit(): void {
    broadcastResponseToMainFrame().catch((error: Error) => {
      console.error(
        'Error al devolver la autenticación:',
        error
      );
    });
  }
}