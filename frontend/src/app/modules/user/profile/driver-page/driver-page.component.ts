import { Component, OnInit } from '@angular/core';
import { faChevronLeft } from '@fortawesome/free-solid-svg-icons';
import { DriverService } from 'src/app/core/http/driver/driver.service';
import { Driver } from 'src/app/shared/models/driver.model';

@Component({
  selector: 'app-driver-page',
  templateUrl: './driver-page.component.html',
})
export class DriverPageComponent implements OnInit {
  faChevronLeft = faChevronLeft;
  driver!: Driver;
  
  constructor(private driverService: DriverService) { }

  ngOnInit(): void {
    this.driverService
      .getDriverByUsername('driver1')
      .then((response) => {
        this.driver = response.data;
        console.log(this.driver);
      });
  }

}
