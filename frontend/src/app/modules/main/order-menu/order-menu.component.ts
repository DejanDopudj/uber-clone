import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IconDefinition, faChevronRight, faChevronLeft, faChevronUp, faChevronDown, faCircle, faFlagCheckered, faStop, faPlus, faXmark, faStopwatch, faRoute, faPaw, faBabyCarriage, faHandHoldingUsd } from '@fortawesome/free-solid-svg-icons';
import { AuthenticationService } from 'src/app/core/authentication/authentication.service';
import { RideService } from 'src/app/core/http/ride/ride.service';
import { RideSummary } from 'src/app/shared/models/data-transfer-interfaces/ride-summary.model';
import { VehicleType } from 'src/app/shared/models/vehicle-type.model';

@Component({
  selector: 'app-order-menu',
  templateUrl: './order-menu.component.html',
  styleUrls: ['./order-menu.component.css']
})
export class OrderMenuComponent implements OnInit {
  @Input() waypoints: any[] = [];
  @Input() summary!: RideSummary;
  @Output() stopAdded: EventEmitter<string> = new EventEmitter<string>();
  @Output() stopRemoved: EventEmitter<Number> = new EventEmitter<Number>();

  faChevronRight: IconDefinition = faChevronRight;
  faChevronLeft: IconDefinition = faChevronLeft;
  faChevronUp: IconDefinition = faChevronUp;
  faChevronDown: IconDefinition = faChevronDown;
  faCircle: IconDefinition = faCircle;
  faStop: IconDefinition = faStop;
  faPlus: IconDefinition = faPlus;
  faFlagCheckered: IconDefinition = faFlagCheckered;
  faXmark: IconDefinition = faXmark;
  faStopwatch: IconDefinition = faStopwatch;
  faRoute: IconDefinition = faRoute;
  faBabyCarriage: IconDefinition = faBabyCarriage;
  faPaw: IconDefinition = faPaw;
  faHandHoldingUsd: IconDefinition = faHandHoldingUsd;

  accountType: string = this.authenticationService.getAccountType();
  isOpened: boolean = false;
  newStopQuery: string = '';

  coupeImg: string = 'assets/icons/car-coupe.png';
  minivanImg: string = 'assets/icons/car-minivan-gray.png';
  stationImg: string = 'assets/icons/car-station-gray.png';

  selectedVehicleType: string = 'coupe';
  hasBabySeat: boolean = false;
  isPetFriendly: boolean = false;

  vehicleTypes: VehicleType[] = [];

  constructor(private authenticationService: AuthenticationService, private rideService: RideService) { }

  async ngOnInit(): Promise<void> {
    await this.loadVehicleTypes();
  }

  calculateRidePrice(): Number {
    const vehicleType: VehicleType | undefined = this.vehicleTypes.find(x => x.name === this.selectedVehicleType.toUpperCase());
    if (vehicleType)
      return Number((vehicleType.price + Number((this.summary.totalDistance / 1000).toFixed(2)) * 120).toFixed(0));
    return -1;
  }

  addStop(): void {
    this.stopAdded.emit(this.newStopQuery);
    this.newStopQuery = '';
  }

  removeStop(i: Number): void {
    this.stopRemoved.emit(i);
  }

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
      this.loadVehicleTypes();
    }
    else {
      setTimeout(() => { this.isOpened = false; }, 250);
      document.getElementById('order-menu')?.classList.remove(inAnimation);
      document.getElementById('order-menu')?.classList.add(outAnimation);
    }
  }

  async loadVehicleTypes(): Promise<void> {
    if (this.vehicleTypes.length === 0)
      this.vehicleTypes = await this.rideService.getVehicleTypes();
  }

  getIcon(i: Number) {
    if (i === 0) return this.faCircle;
    else if (i === this.waypoints.length - 1) return this.faFlagCheckered;
    else return this.faStop;
  }
}
