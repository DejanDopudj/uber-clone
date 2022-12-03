import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountComponent } from './profile/account.component';
import { UserRoutingModule } from './user-routing.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { DriverProfileComponent } from './profile/driver-profile/driver-profile.component';
import { MemberProfileComponent } from './profile/member-profile/member-profile.component';
import { DriverPageComponent } from './profile/driver-page/driver-page.component';



@NgModule({
  declarations: [
    AccountComponent,
    DriverProfileComponent,
    MemberProfileComponent,
    DriverPageComponent
  ],
  imports: [
    CommonModule,
    UserRoutingModule,
    FontAwesomeModule
  ]
})
export class UserModule { }
