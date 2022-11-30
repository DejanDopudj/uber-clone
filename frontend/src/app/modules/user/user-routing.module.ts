import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProfileComponent } from './profile/profile.component';
const routes: Routes = [
  { path: '', component: ProfileComponent },
  { path: '**', redirectTo: '/auth/404' },
];

@NgModule({
  declarations: [],
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [ RouterModule ]
})
export class UserRoutingModule { }
