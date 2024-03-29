import { Component, OnInit } from '@angular/core';
import {
  IconDefinition,
  faChevronLeft,
  faStar,
  faChevronCircleDown,
  faUser,
  faTaxi,
} from '@fortawesome/free-solid-svg-icons';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import * as GeoSearch from 'leaflet-geosearch';
import { PassengerRide } from 'src/app/shared/models/ride.model';
import * as moment from 'moment';
import { Driver } from 'src/app/shared/models/driver.model';
import { PhotoService } from 'src/app/core/http/user/photo.service';
import { DriverService } from 'src/app/core/http/user/driver.service';

const service_url = 'https://nominatim.openstreetmap.org/reverse?format=json';
const API_KEY = null;

@Component({
  selector: 'app-ride-history',
  templateUrl: './ride-history.component.html',
})
export class RideHistoryComponent implements OnInit {
  faChevronLeft: IconDefinition = faChevronLeft;
  faStar: IconDefinition = faStar;
  faUser: IconDefinition = faUser;
  faTaxi: IconDefinition = faTaxi;

  private map: any;
  private control: any;
  private provider!: GeoSearch.OpenStreetMapProvider;
  faChevronCircleDown: IconDefinition = faChevronCircleDown;
  sortBy: string = 'startTime';
  startElem: number = 0;
  numOfElements: number = 0;
  page: number = 0;
  selectedRide: PassengerRide | null = null;
  selectedIsFavourite: boolean = false;
  rides: Array<PassengerRide> = [];
  passengers: Array<Driver> = [];

  showReviewModal: boolean = false;

  constructor(
    private driverService: DriverService,
    private photoService: PhotoService
  ) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.initMap();
      this.getRides();
    }, 10);
  }

  private initMap(): void {
    L.Marker.prototype.options.icon = L.icon({
      iconUrl: './assets/icons/alternative-marker.png',
      iconSize: [30, 45],
      iconAnchor: [15, 45],
    });

    this.map = L.map('map', {
      center: [45.254326, 19.827178],
      zoom: 13,
    });

    const tiles = L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      {
        maxZoom: 19,
        minZoom: 3,
        attribution:
          '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      }
    );
    tiles.addTo(this.map);

    this.provider = new GeoSearch.OpenStreetMapProvider();

    this.control = L.Routing.control({
      lineOptions: {
        styles: [{ color: '#ff7035', weight: 4 }],
        extendToWaypoints: true,
        missingRouteTolerance: 0.1,
      },
      showAlternatives: false,
      addWaypoints: false,
      waypoints: [],
      autoRoute: true,
      routeWhileDragging: true,
      useZoomParameter: true,
    }).addTo(this.map);
  }

  selectRide(id: number): void {
    const newRide = this.rides.find((ride) => ride.id === id);
    if (newRide === this.selectedRide) return;
    this.selectedRide! = this.rides.find((ride) => ride.id === id)!;
    this.map.setView(this.selectedRide.route.coordinates[0], 8);
    this.control.setWaypoints(this.selectedRide!.route.waypoints);
  }

  sortRides(event: Event): void {
    const sortBy = (event.target as HTMLInputElement).value;
    this.sortBy = sortBy;
    this.getRides();
  }

  getRides(): void {
    this.driverService.getRides(this.page, 4, this.sortBy, '').then((res) => {
      this.startElem = this.page * 4;
      this.numOfElements = res.data.totalElements;
      this.rides = res.data.content;
      this.selectedRide = this.rides[0];
      if (this.selectedRide) {
        this.control.setWaypoints(
          this.selectedRide.route.waypoints
        );
        this.driverService.getRideDetails(this.selectedRide.id).then((res) => {
          this.passengers = res.data.passengers;
          for (let passenger of this.passengers) {
            this.getImage(passenger.profilePicture);
          }
        });
      }
    });
  }

  prev(): void {
    this.page--;
    this.getRides();
  }

  next(): void {
    this.page++;
    this.getRides();
  }

  openReviewModal(): void {
    this.showReviewModal = true;
  }

  onReviewSent(): void {
    this.showReviewModal = false;
    this.getRides();
  }

  canUserRateRide(): boolean {
    if (!this.selectedRide) return false;
    if (this.selectedRide.driverRating || this.selectedRide.vehicleRating)
      return false;
    if (moment().diff(moment(this.selectedRide.endTime), 'hours') > 72)
      return false;
    return true;
  }

  getDateTime(date: Date): string {
    let tempDate = new Date(date);
    return tempDate.toLocaleString('en-GB');
  }

  getRideStatus(ride: PassengerRide): string {
    return ride.status.replace(/_/g, ' ');
  }

  setArrayFromNumber(i: number) {
    return new Array(i);
  }

  getImage(profilePicture: string): void {
    this.photoService.loadImage(profilePicture).then((response) => {
      for (let passenger of this.passengers) {
        if (passenger.profilePicture === profilePicture) {
          passenger.userImage = response.data;
        }
      }
    });
  }

  goHome(): void {
    window.location.href = '/';
  }
}
