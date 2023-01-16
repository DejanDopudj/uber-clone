import { Injectable } from '@angular/core';
import axios from 'axios';
import { RideSimple } from 'src/app/shared/models/ride.model';
import { AuthenticationService } from '../../authentication/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class PassengerService {
  private currentRide: RideSimple | null = null;

  constructor(private authenticationService: AuthenticationService) { }

  getPassengerByUsername(username: string): Promise<any> {
    return axios.get(`/api/passenger/${username}`);
  }

  async addTokens(amount: number): Promise<void> {
    var tokenData = {
        "intent" : "CAPTURE",
        "purchase_units" : [
            {
                "amount" : {
                    "currency_code": "EUR",
                    "value" : amount
                }
            }
        ]
    };
    
    await axios.post(`/api/checkout`, tokenData, {
      headers: {
        Authorization: `Bearer ${this.authenticationService.getToken()}`
      }
    }).then((resp => {
      window.open(resp.data.links[1].href, '_blank');
    }));
  }

  // SOCKET WILL TELL US WHEN THE RIDE IS OVER OR REJECTED SO WE CAN UPDATE THIS

  fetchCurrentRide = async () => {
    await axios.get(`/api/passengers/current-ride`, {
      headers: {
        Authorization: `Bearer ${this.authenticationService.getToken()}`
      }
    })
    .then((res) => {
      if (res.data) this.currentRide = res.data;
    })
    .catch((err) => {
      this.currentRide = null;
    });
  }

  async register(data: any): Promise<any> {
    await axios.post(`/api/passengers`, data, {
      headers: {
        Authorization: `Bearer ${this.authenticationService.getToken()}`
      }
    }).then((res => {
      return res.data;
    }));
  }

  getCurrentRide = () => {
    return this.currentRide;
  }

  setCurrentRide = (ride: RideSimple) => {
    this.currentRide = ride;
  }
}
