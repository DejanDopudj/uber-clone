import { Component, Input, OnInit } from '@angular/core';
import { faStar, faTaxi, faUser, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { DriverService } from 'src/app/core/http/user/driver.service';
import { PhotoService } from 'src/app/core/http/user/photo.service';
import { Driver } from 'src/app/shared/models/driver.model';
import { DriverReview } from 'src/app/shared/models/review.model';


@Component({
  selector: 'app-driver-reviews',
  templateUrl: './driver-reviews.component.html',
})
export class DriverReviewsComponent implements OnInit {
  @Input() driver!: Driver;
  
  faStar: IconDefinition = faStar;
  faUser: IconDefinition = faUser;
  faTaxi: IconDefinition = faTaxi;

  reviews: DriverReview[] = [];
  startElem: number = 0;
  numOfElements: number = 0;
  page: number = 0;
  readonly RESULTS_PER_PAGE: number = 5;

  constructor(
    private driverService: DriverService,
    private photoService: PhotoService
    ) { }

  ngOnInit(): void {
    this.getReviews();
  }

  getReviews(): void {
    this.driverService.fetchReviews(this.driver.username, this.page, this.RESULTS_PER_PAGE)
    .then((res) => {
      this.startElem = this.page * this.RESULTS_PER_PAGE;
      this.numOfElements = res.data.totalElements;
      this.reviews = res.data.content;
      this.fetchImages();
    });
  }

  fetchImages(): void {
    this.reviews.map(review => review.passenger)
    .forEach(passenger => this.getImage(passenger.profilePicture));
  }

  getImage(profilePicture: string): void {
    this.photoService.loadImage(profilePicture).then((response) => {
      for (let passenger of this.reviews.map(review => review.passenger)) {
        if (passenger.profilePicture === profilePicture) {
          passenger.profilePicture = response.data;
        }
      }
    });
  }

  prev(): void {
    this.page--;
    this.getReviews();
  }

  next(): void {
    this.page++;
    this.getReviews();
  }

  setArrayFromNumber(i: number) {
    return new Array(i);
  }

}
