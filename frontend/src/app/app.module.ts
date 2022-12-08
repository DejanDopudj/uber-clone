import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AppComponent } from './app.component';
import { MapComponent } from './modules/map/map.component';
import { MapControlsComponent } from './modules/map/map-controls/map-controls.component';
import { AuthModule } from './modules/auth/auth.module';
import { AuthenticationService } from './core/authentication/authentication.service';

@NgModule({
  declarations: [
    AppComponent,
    MapComponent,
    MapControlsComponent,
  ],
  imports: [
    BrowserModule, AppRoutingModule, FontAwesomeModule, AuthModule
  ],
  providers: [AuthenticationService],
  bootstrap: [AppComponent]
})
export class AppModule { }
