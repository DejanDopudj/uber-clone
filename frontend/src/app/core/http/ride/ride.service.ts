import { Injectable } from '@angular/core';
import axios from 'axios';


@Injectable({
  providedIn: 'root'
})
export class RideService {

  constructor() { }

  getVehicleTypes(): Promise<any> {
    return axios.get(`/api/vehicles/types`).then((res => {
      return res.data;
    }));
  }

}
