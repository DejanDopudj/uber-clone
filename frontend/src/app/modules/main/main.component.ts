import { Component, OnInit, ViewChild } from '@angular/core';
import { AuthenticationService } from 'src/app/core/authentication/authentication.service';
import { DriverService } from 'src/app/core/http/user/driver.service';
import { MapComponent } from './map/map.component';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
})
export class MainComponent implements OnInit {
  @ViewChild(MapComponent, { static: true }) mapComponent!: MapComponent;

  // All
  accountType: string = 'anonymous';

  // Driver
  isActive: boolean | null = null;

  constructor(private driverService: DriverService, private authenticationService: AuthenticationService) { }

  async ngOnInit(): Promise<void> {
    this.accountType = this.authenticationService.getAccountType();
    if (this.accountType === 'driver')
      this.isActive = await this.driverService.getDriverActivity();
  }

  addNewStop(event: string): void {
    this.mapComponent.addNewWaypoint(event);
  }

  removeStop(event: Number): void {
    this.mapComponent.removeWaypoint(event);
  }

  clearMarkers(): void {
    this.mapComponent.clearMarkers();
  }

  get summary(): any {
    return this.mapComponent.chosenRoute?.summary;
  }

  get waypoints(): any {
    return this.mapComponent.waypoints;
  }
}
