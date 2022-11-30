import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileComponent } from './profile/profile.component';
import { UserRoutingModule } from './user-routing.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';



@NgModule({
  declarations: [
    ProfileComponent
  ],
  imports: [
    CommonModule,
    UserRoutingModule,
    FontAwesomeModule
  ]
})
export class UserModule { }
