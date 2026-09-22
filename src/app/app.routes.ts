import { Routes } from '@angular/router';
import { MsalGuard } from '@azure/msal-angular';

import { Inicio } from './pages/inicio/inicio';
import { Pedidos } from './pages/pedidos/pedidos';
import { Redirect } from './redirect/redirect';

export const routes: Routes = [
  {
    path: 'redirect',
    component: Redirect
  },
  {
    path: '',
    component: Inicio
  },
  {
    path: 'pedidos',
    component: Pedidos,
    canActivate: [MsalGuard]
  },
  {
    path: '**',
    redirectTo: ''
  }
];