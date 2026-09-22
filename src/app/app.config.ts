import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners
} from '@angular/core';

import { provideRouter } from '@angular/router';

import {
  provideHttpClient,
  withInterceptorsFromDi,
  HTTP_INTERCEPTORS
} from '@angular/common/http';

import {
  IPublicClientApplication,
  PublicClientApplication,
  InteractionType
} from '@azure/msal-browser';

import {
  MSAL_INSTANCE,
  MSAL_GUARD_CONFIG,
  MSAL_INTERCEPTOR_CONFIG,
  MsalGuard,
  MsalInterceptor,
  MsalService,
  MsalBroadcastService,
  MsalGuardConfiguration,
  MsalInterceptorConfiguration
} from '@azure/msal-angular';

import { routes } from './app.routes';


export function MSALInstanceFactory(): IPublicClientApplication {
  return new PublicClientApplication({
    auth: {
      clientId: 'bb47a6a0-1b96-43fb-b107-1576638ee9e8',

      authority:
        'https://login.microsoftonline.com/bb5324af-c266-41ed-b36c-a971641c7af2',

      redirectUri: 'http://localhost:4200/redirect',

      postLogoutRedirectUri: 'http://localhost:4200'
    },

    cache: {
      cacheLocation: 'localStorage'
    }
  });
}


export function MSALGuardConfigFactory(): MsalGuardConfiguration {
  return {
    interactionType: InteractionType.Redirect,

    authRequest: {
      scopes: [
        'openid',
        'profile',
        'api://bb47a6a0-1b96-43fb-b107-1576638ee9e8/Pedidos.ReadWrite'
      ]
    }
  };
}


export function MSALInterceptorConfigFactory():
  MsalInterceptorConfiguration {

  const protectedResourceMap =
    new Map<string, Array<string>>([
      [
        'http://localhost:8080/api/pedidos',
        [
          'api://bb47a6a0-1b96-43fb-b107-1576638ee9e8/Pedidos.ReadWrite'
        ]
      ]
    ]);

  return {
    interactionType: InteractionType.Redirect,
    protectedResourceMap
  };
}


export const appConfig: ApplicationConfig = {
  providers: [

    provideBrowserGlobalErrorListeners(),

    provideRouter(routes),

    provideHttpClient(
      withInterceptorsFromDi()
    ),

    {
      provide: MSAL_INSTANCE,
      useFactory: MSALInstanceFactory
    },

    {
      provide: MSAL_GUARD_CONFIG,
      useFactory: MSALGuardConfigFactory
    },

    {
      provide: MSAL_INTERCEPTOR_CONFIG,
      useFactory: MSALInterceptorConfigFactory
    },

    MsalService,
    MsalGuard,
    MsalBroadcastService,

    {
      provide: HTTP_INTERCEPTORS,
      useClass: MsalInterceptor,
      multi: true
    }
  ]
};