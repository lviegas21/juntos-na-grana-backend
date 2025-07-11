import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import AppUserResolve from './route/app-user-routing-resolve.service';

const appUserRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/app-user.component').then(m => m.AppUserComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/app-user-detail.component').then(m => m.AppUserDetailComponent),
    resolve: {
      appUser: AppUserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/app-user-update.component').then(m => m.AppUserUpdateComponent),
    resolve: {
      appUser: AppUserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/app-user-update.component').then(m => m.AppUserUpdateComponent),
    resolve: {
      appUser: AppUserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default appUserRoute;
