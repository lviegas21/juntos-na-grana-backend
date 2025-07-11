import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import FamilyResolve from './route/family-routing-resolve.service';

const familyRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/family.component').then(m => m.FamilyComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/family-detail.component').then(m => m.FamilyDetailComponent),
    resolve: {
      family: FamilyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/family-update.component').then(m => m.FamilyUpdateComponent),
    resolve: {
      family: FamilyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/family-update.component').then(m => m.FamilyUpdateComponent),
    resolve: {
      family: FamilyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default familyRoute;
