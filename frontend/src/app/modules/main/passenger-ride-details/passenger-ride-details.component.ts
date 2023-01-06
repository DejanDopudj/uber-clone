import { Component, Input, OnInit } from '@angular/core';
import { faChevronDown, faChevronLeft, faChevronRight, faChevronUp, faCircle, faFlagCheckered, faHandHoldingUsd, faRoute, faStop, faStopwatch, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { AuthenticationService } from 'src/app/core/authentication/authentication.service';
import { RideService } from 'src/app/core/http/ride/ride.service';
import { PassengerService } from 'src/app/core/http/user/passenger.service';
import { VehicleType } from 'src/app/shared/models/vehicle-type.model';

@Component({
  selector: 'app-passenger-ride-details',
  templateUrl: './passenger-ride-details.component.html',
  styleUrls: ['./passenger-ride-details.component.css']
})
export class PassengerRideDetailsComponent implements OnInit {
  @Input() waypoints: any[] = [];

  faChevronRight: IconDefinition = faChevronRight;
  faChevronLeft: IconDefinition = faChevronLeft;
  faChevronUp: IconDefinition = faChevronUp;
  faChevronDown: IconDefinition = faChevronDown;
  faCircle: IconDefinition = faCircle;
  faStop: IconDefinition = faStop;
  faFlagCheckered: IconDefinition = faFlagCheckered;
  faStopwatch: IconDefinition = faStopwatch;
  faRoute: IconDefinition = faRoute;
  faHandHoldingUsd: IconDefinition = faHandHoldingUsd;

  accountType: string = this.authenticationService.getAccountType();
  isOpened: boolean = false;
  newStopQuery: string = '';

  coupeImg: string = 'assets/icons/car-coupe.png';
  minivanImg: string = 'assets/icons/car-minivan-gray.png';
  stationImg: string = 'assets/icons/car-station-gray.png';

  constructor(private authenticationService: AuthenticationService, private passengerService: PassengerService) { }

  ngOnInit(): void { }

  toggleOpened(): void {
    let inAnimation = 'slide-in';
    let outAnimation = 'slide-out';
    if (window.screen.width < 640) {
      inAnimation = 'slide-in-bottom';
      outAnimation = 'slide-out-bottom';
    }
    if (!this.isOpened) {
      this.isOpened = true;
      document.getElementById('order-menu')?.classList.remove(outAnimation);
      document.getElementById('order-menu')?.classList.add(inAnimation);
    }
    else {
      setTimeout(() => { this.isOpened = false; }, 250);
      document.getElementById('order-menu')?.classList.remove(inAnimation);
      document.getElementById('order-menu')?.classList.add(outAnimation);
    }
  }

  getIcon(i: Number) {
    if (i === 0) return this.faCircle;
    else if (i === this.waypoints.length - 1) return this.faFlagCheckered;
    else return this.faStop;
  }

  get ride () {
    return this.passengerService.getCurrentRide();
  }

}
