import { Component, AfterViewInit, Input } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import * as GeoSearch from 'leaflet-geosearch';
import axios from 'axios';
import { DriverInfo } from 'src/app/shared/models/data-transfer-interfaces/driver-info.model';
import { SharedInfo } from 'src/app/shared/models/data-transfer-interfaces/shared-info.model';

const service_url = "https://nominatim.openstreetmap.org/reverse?format=json";
const API_KEY = null;

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements AfterViewInit {
  private map: any;
  private control: any;
  public chosenRoute: any;
  public waypoints: any[] = [];
  @Input() sharedInfo!: SharedInfo;
  @Input() driverInfo!: DriverInfo;
  private provider!: GeoSearch.OpenStreetMapProvider;

  private initMap(): void {
    L.Marker.prototype.options.icon = L.icon({
      iconUrl: './assets/icons/alternative-marker.png',
      iconSize:     [30, 45],
      iconAnchor:   [15, 45],
    });
  
    this.map = L.map('map', {
      center: [ 45.254326, 19.827178 ],
      zoom: 15
    });

    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      minZoom: 3,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    });
    tiles.addTo(this.map);

    this.provider = new GeoSearch.OpenStreetMapProvider();

    this.map.on('click', this.addMarker); 


    const that = this;
    this.control = L.Routing.control({
      lineOptions: {styles: [{color: '#006D5B', weight: 4}], extendToWaypoints: true, missingRouteTolerance: 0.1},
      altLineOptions: {styles: [{color: '#8aa1ad', weight: 7}], extendToWaypoints: true, missingRouteTolerance: 0.1},
      showAlternatives: true,
      addWaypoints: this.canUserAlterWaypoints() ? true : false,
      waypoints: [],
      autoRoute: true,
      routeWhileDragging: true,
      plan: L.Routing.plan([], {
        createMarker: function(i, wp) {
          return L.marker(wp.latLng, {draggable: true}).on('contextmenu', function(e: any) { 
            that.removeMarker(wp);
          });
        }
      })
    })
    .on('routesfound', function(e) {
      const newRoute = e.routes[0];
      // a waypoint was dragged
      if (that.chosenRoute?.waypoints.length === newRoute.waypoints.length)
        that.updateWaypoints(that.chosenRoute.waypoints, newRoute.waypoints);
      that.chosenRoute = newRoute;
    })
    .on('routeselected', function(e) {
      that.chosenRoute = e.route;
    })
    .addTo(this.map);

    if (this.canUserAlterWaypoints())
      document.getElementById('map')!.style.cursor = 'crosshair';
  }

  constructor() { }

  ngAfterViewInit(): void {
    this.initMap();
  }

  addMarker = async (e: any) => {
    if (this.canUserAlterWaypoints()) {
      this.control.setWaypoints([...this.control.getPlan().getWaypoints().filter((x: L.Routing.Waypoint) => x.latLng), e.latlng]);
      await this.reverseSearchLocation(e.latlng.lat, e.latlng.lng)
      .then((res) => {
        this.waypoints.push(res);
      });
    }
  }

  removeMarker(wp: any): void {
    if (this.canUserAlterWaypoints()) {
      const i = this.control.getWaypoints().findIndex((x: any) => x === wp);
      this.control.setWaypoints([...this.control.getPlan().getWaypoints().filter((x: L.Routing.Waypoint) => x !== wp)]);
      this.waypoints.splice(i, 1);
    }
  }

  clearMarkers(): void {
    this.control.setWaypoints([]);
    this.chosenRoute = null;
    this.waypoints = [];
  }

  drawRoute(route: L.Routing.IRoute): void {
    L.Routing.line(route, {styles: [{color: '#006D5B', weight: 4}], extendToWaypoints: true, missingRouteTolerance: 0.1}).addTo(this.map);
    route.waypoints?.forEach((e : any) => {
      L.marker(e.latLng).addTo(this.map);
    });
  }

  canUserAlterWaypoints(): boolean {
    return this.sharedInfo.accountType === 'passenger' || this.sharedInfo.accountType === 'anonymous';
  }

  async updateWaypoints(oldRouteWaypoints: any[], newRouteWaypoints: any[]): Promise<void> {
    for (let i = 0; i < oldRouteWaypoints.length; i++) {
      if (!this.areSameCoordinates(oldRouteWaypoints[i].latLng, newRouteWaypoints[i].latLng)) {
        await this.reverseSearchLocation(newRouteWaypoints[i].latLng.lat, newRouteWaypoints[i].latLng.lng)
        .then((res) => {
          this.waypoints[i] = res;
        });
      }
    }
  }

  private areSameCoordinates(x: any, y: any): boolean {
    return x.lat === y.lat && x.lng === y.lng;
  }

  async addNewWaypoint(event: string) {
    const results = await this.searchLocation(event);
    if (results.length > 0)
      this.addMarker({ latlng: { lat: results[0].y, lng: results[0].x } });
  }

  removeWaypoint(event: any) {
    this.removeMarker(this.control.getWaypoints()[event])
  }

  searchLocation = async (query: string) => {
    return await this.provider.search({ query: query }).then((res) => {
      return res;
    });
  }

  reverseSearchLocation = async ( latitude: number, longitude: number, zoom: number = 18 ): Promise<any> => {
    let url = `${service_url}&lat=${latitude}&lon=${longitude}&zoom=${zoom}`;
    url = API_KEY ? `${url}&key=${API_KEY}` : url;
    try {
        const response = await axios.get(url);
        return {
            x: response.data.lon,
            y: response.data.lat,
            label: response.data.display_name,
            bounds: response.data.boundingbox,
            raw: response.data
        }
    } catch (error) {
        console.error(error);
    }
}

}
