import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { faBars, faArrowRightToBracket } from '@fortawesome/free-solid-svg-icons';
import { AuthenticationService } from 'src/app/core/authentication/authentication.service';
import { DriverService } from 'src/app/core/http/user/driver.service';

interface RideSummary {
  totalDistance: number,
  totalTime: number,
}

@Component({
  selector: 'app-map-controls',
  templateUrl: './map-controls.component.html',
})
export class MapControlsComponent implements OnInit {
  @Input() summary!: RideSummary;
  @Output() clear: EventEmitter<any> = new EventEmitter();

  // All
  accountType: string = this.authenticationService.getAccountType();

  // Driver
  isActive: boolean = this.driverService.getDriverActivity();


  faBars = faBars;
  faArrowRightToBracket = faArrowRightToBracket;
  
  constructor(private driverService: DriverService, private authenticationService: AuthenticationService) { }

  ngOnInit(): void {
  }

  clearMarkers = (): void => {
    this.clear.emit();
  }

  toggleActivity = (): void => {
    this.isActive = !this.isActive;
    this.driverService.toggleActivity();
  }
}
