import { Coordinates } from './coordinates.model';
import { DriverSimple } from './driver.model';
import { PassengerSimple } from './passenger.model';
import { Route } from './route.model';

export interface RideSimple {
  id: number;
  distance: number;
  expectedTime: number;
  price: number;
  driver: DriverSimple;
  route: Route;
  createdAt: Date;
  allConfirmed: boolean;
  passengerConfirmed: boolean;
  status: string;
  startAddress: string;
  destinationAddress: string;
  delayInMinutes: number;
  driverInconsistencyReported: boolean;
}

export interface DriverRide {
  id: number;
  distance: number;
  expectedTime: number;
  price: number;
  passengers: PassengerSimple[];
  route: Route;
  createdAt: Date;
  status: string;
  startAddress: string;
  destinationAddress: string;
}

export interface PassengerRide {
  id: number;
  distance: number;
  price: number;
  startAddress: string;
  destinationAddress: string;
  vehicleType: string;
  startTime: Date;
  endTime: Date;
  expectedTime: Date;
  status: string;
  route: {
    coordinates: Coordinates[];
    waypoints: Coordinates[];
  };
  driverRating: number;
  vehicleRating: number;
}
