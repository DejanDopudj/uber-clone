import { Component, OnInit } from '@angular/core';
import {
  IconDefinition,
  faChevronLeft,
  faStar,
} from '@fortawesome/free-solid-svg-icons';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import * as GeoSearch from 'leaflet-geosearch';
import { PassengerRide } from 'src/app/shared/models/ride.model';
import { PassengerService } from 'src/app/core/http/user/passenger.service';

const service_url = 'https://nominatim.openstreetmap.org/reverse?format=json';
const API_KEY = null;

@Component({
  selector: 'app-ride-history',
  templateUrl: './ride-history.component.html',
})
export class RideHistoryComponent implements OnInit {
  private map: any;
  private control: any;
  private provider!: GeoSearch.OpenStreetMapProvider;
  faChevronLeft: IconDefinition = faChevronLeft;
  faStar: IconDefinition = faStar;
  startElem: number = 0;
  numOfElements: number = 0;
  page: number = 0;
  selectedRide: PassengerRide | null = null;
  selectedIsFavourite: boolean = false;
  rides: Array<PassengerRide> = [
    {
      id: 1,
      distance: 100,
      price: 100,
      startAddress: 'address',
      destinationAddress: 'address',
      vehicleType: 'COUPE',
      startTime: '2021-01-01 12:00',
      endTime: '2021-01-01 12:10',
      expectedTime: 10,
      status: 'CANCELLED',
      route: {
        coordinates: [
          { lat: 45.252782, lng: 19.855517 },
          { lat: 45.245749, lng: 19.851122 },
        ],
        waypoints: [
          { lat: 45.252782, lng: 19.855517 },
          { lat: 45.245749, lng: 19.851122 },
        ],
      },
      isFavourite: true,
    },
    {
      id: 2,
      distance: 100,
      price: 100,
      startAddress: 'address',
      destinationAddress: 'address',
      vehicleType: 'MINIVAN',
      startTime: '2021-01-01 12:00',
      endTime: '2021-01-01 12:10',
      expectedTime: 10,
      status: 'COMPLETED',
      route: {
        coordinates: [
          { lat: 45.241805, lng: 19.798567 },
          { lat: 45.252782, lng: 19.855517 },
        ],
        waypoints: [
          { lat: 45.241805, lng: 19.798567 },
          { lat: 45.252782, lng: 19.855517 },
        ],
      },
      isFavourite: false,
    },
    {
      id: 3,
      distance: 100,
      price: 100,
      startAddress: ' sadas dsadsadas dsa das das ',
      destinationAddress: 'das das das d asd as das ',
      vehicleType: 'STATION',
      startTime: '2021-01-01 11:00',
      endTime: '2021-01-01 11:10',
      expectedTime: 10,
      status: 'COMPLETED',
      route: {
        coordinates: [
          { lat: 45.241805, lng: 19.798567 },
          { lat: 45.245749, lng: 19.851122 },
        ],
        waypoints: [
          { lat: 45.241805, lng: 19.798567 },
          { lat: 45.245749, lng: 19.851122 },
        ],
      },
      isFavourite: true,
    },
    {
      id: 4,
      distance: 100,
      price: 100,
      startAddress: ' sadas dsadsadas dsa das das ',
      destinationAddress: 'das das das d asd as das ',
      vehicleType: 'STATION',
      startTime: '2021-01-01 11:00',
      endTime: '2021-01-01 11:10',
      expectedTime: 10,
      status: 'COMPLETED',
      route: {
        coordinates: [
          { lat: 45.241805, lng: 19.798567 },
          { lat: 45.245749, lng: 19.851122 },
        ],
        waypoints: [
          { lat: 45.241805, lng: 19.798567 },
          { lat: 45.245749, lng: 19.851122 },
        ],
      },
      isFavourite: false,
    },
    {
      id: 5,
      distance: 100,
      price: 100,
      startAddress: ' sadas dsadsadas dsa das das ',
      destinationAddress: 'das das das d asd as das ',
      vehicleType: 'STATION',
      startTime: '2021-01-01 11:00',
      endTime: '2021-01-01 11:10',
      expectedTime: 10,
      status: 'COMPLETED',
      route: {
        coordinates: [
          { lat: 45.241805, lng: 19.798567 },
          { lat: 45.245749, lng: 19.851122 },
        ],
        waypoints: [
          { lat: 45.241805, lng: 19.798567 },
          { lat: 45.245749, lng: 19.851122 },
        ],
      },
      isFavourite: false,
    },
    {
      id: 6,
      distance: 100,
      price: 100,
      startAddress: 'address',
      destinationAddress: 'address',
      vehicleType: 'COUPE',
      startTime: '2021-01-01 12:00',
      endTime: '2021-01-01 12:10',
      expectedTime: 10,
      status: 'CANCELLED',
      route: {
        coordinates: [
          { lat: 45.252782, lng: 19.855517 },
          { lat: 45.245749, lng: 19.851122 },
        ],
        waypoints: [
          { lat: 45.252782, lng: 19.855517 },
          { lat: 45.245749, lng: 19.851122 },
        ],
      },
      isFavourite: true,
    },
  ];
  constructor(private passengerService: PassengerService) {}

  ngOnInit(): void {
    this.selectedRide = this.rides[0];
    this.initMap();
    this.control.setWaypoints([
      this.selectedRide.route.waypoints[0],
      this.selectedRide.route.waypoints[1],
    ]);
    this.selectedIsFavourite = this.selectedRide!.isFavourite;
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
    this.control.setWaypoints([
      this.selectedRide!.route.waypoints[0],
      this.selectedRide!.route.waypoints[1],
    ]);
    this.selectedIsFavourite = this.selectedRide!.isFavourite;
  }

  prev(): void {
    this.page--;
    this.passengerService.getRides(this.page, 7, 'Id').then((res) => {
      console.log(res);
    });
  }

  next(): void {
    this.page++;
    this.passengerService.getRides(this.page, 7, 'Id').then((res) => {
      console.log(res);
    });
  }

  changeFavouriteStatus(): void {
    this.selectedRide!.isFavourite = !this.selectedRide!.isFavourite;
    if (!this.selectedIsFavourite)
      this.passengerService.markFavouriteRoute(this.selectedRide!.id);
    else this.passengerService.unmarkFavouriteRoute(this.selectedRide!.id);
    this.selectedIsFavourite = this.selectedRide!.isFavourite;
  }
}
