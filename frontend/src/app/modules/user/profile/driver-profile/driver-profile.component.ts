import { Component, OnInit, Input } from '@angular/core';
import { faCar, faPaperPlane, faMobileRetro, faBabyCarriage, faPaw } from '@fortawesome/free-solid-svg-icons';
import { Driver } from 'src/app/shared/models/driver.model';

@Component({
  selector: 'app-driver',
  templateUrl: './driver-profile.component.html'
})
export class DriverProfileComponent implements OnInit {
  @Input() driver!: Driver;

  faCar = faCar;
  faPaperPlane = faPaperPlane;
  faMobileRetro = faMobileRetro;
  faBabyCarriage = faBabyCarriage;
  faPaw = faPaw;

  constructor() { }

  ngOnInit(): void {
  }

  getCarImage(): string {
    switch (this.driver.vehicle.vehicleType.name.toLocaleLowerCase()) {
      case "coupe":
        return 'assets/icons/car-coupe.png';
      case "minivan":
        return 'assets/icons/car-minivan.png';
      case "station":
        return 'assets/icons/car-station.png';
      default:
        return 'assets/icons/car-coupe.png';
    }
  }

}
