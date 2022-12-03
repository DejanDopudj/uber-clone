import { Vehicle } from "./vehicle.model";


export interface Driver {
  username: string,
  name: string,
  surname: string,
  phoneNumber: string,
  city: String,
  profilePicture: string,
  vehicle: Vehicle,
  active: boolean,
}
