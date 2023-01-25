import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FavouriteRoutesComponent } from './favourite-routes/favourite-routes.component';
import { RideHistoryComponent } from './ride-history/ride-history.component';

const routes: Routes = [
  { path: 'fav-routes', component: FavouriteRoutesComponent },
  { path: 'ride-history', component: RideHistoryComponent },
  { path: '**', redirectTo: '/auth/404' },
];

@NgModule({
  declarations: [],
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PassengerRoutingModule {}
