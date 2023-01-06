import { DriverSimple } from "./driver.model";

export interface RideSimple {
  id: Number,
  distance: Number,
  price: Number,
  driver: DriverSimple,
  createdAt: Date,
}
