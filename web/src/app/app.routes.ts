import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'beneficios', pathMatch: 'full' },
  {
    path: 'beneficios',
    loadComponent: () =>
      import('./beneficio-list/beneficio-list.component')
        .then(m => m.BeneficioListComponent)
  },
  {
    path: 'beneficios/novo',
    loadComponent: () =>
      import('./beneficio-form/beneficio-form.component')
        .then(m => m.BeneficioFormComponent)
  },
  {
    path: 'beneficios/:id/editar',
    loadComponent: () =>
      import('./beneficio-form/beneficio-form.component')
        .then(m => m.BeneficioFormComponent)
  },
  { path: '**', redirectTo: 'beneficios' }
];
