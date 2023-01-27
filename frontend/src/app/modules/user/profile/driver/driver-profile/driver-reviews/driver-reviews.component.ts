import { Component, Input, OnInit } from '@angular/core';
import { DriverService } from 'src/app/core/http/user/driver.service';
import { Driver } from 'src/app/shared/models/driver.model';
import { DriverReview } from 'src/app/shared/models/review.model';

@Component({
  selector: 'app-driver-reviews',
  templateUrl: './driver-reviews.component.html',
})
export class DriverReviewsComponent implements OnInit {
  @Input() driver!: Driver;
  reviews: DriverReview[] = [];

  constructor(private driverService: DriverService) { }

  ngOnInit(): void {
    this.driverService.fetchReviews(this.driver.username, 0, 10)
    .then((res) => {
      console.log(res.data);
    });
  }

}
