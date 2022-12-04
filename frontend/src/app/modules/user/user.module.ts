import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountComponent } from './profile/account.component';
import { UserRoutingModule } from './user-routing.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { DriverProfileComponent } from './profile/driver-profile/driver-profile.component';
import { PassengerProfileComponent } from './profile/passenger-profile/passenger-profile.component';
import { DriverPageComponent } from './profile/driver/driver.component';
import { DriverDetailsComponent } from './profile/driver-profile/driver-details/driver-details.component';
import { DriverReviewsComponent } from './profile/driver-profile/driver-reviews/driver-reviews.component';



@NgModule({
  declarations: [
    AccountComponent,
    DriverProfileComponent,
    PassengerProfileComponent,
    DriverPageComponent,
    DriverDetailsComponent,
    DriverReviewsComponent
  ],
  imports: [
    CommonModule,
    UserRoutingModule,
    FontAwesomeModule
  ]
})
export class UserModule { }
