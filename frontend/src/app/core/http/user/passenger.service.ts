import { Injectable } from '@angular/core';
import axios from 'axios';
import { PassengerRide, RideSimple } from 'src/app/shared/models/ride.model';
import { AuthenticationService } from '../../authentication/authentication.service';

@Injectable({
  providedIn: 'root',
})
export class PassengerService {
  private currentRide: RideSimple | null = null;

  constructor(private authenticationService: AuthenticationService) {}

  getPassengerByUsername(username: string): Promise<any> {
    return axios.get(`/api/passenger/${username}`);
  }

  async addTokens(amount: number): Promise<void> {
    var tokenData = {
      intent: 'CAPTURE',
      purchase_units: [
        {
          amount: {
            currency_code: 'EUR',
            value: amount,
          },
        },
      ],
    };

    await axios
      .post(`/api/checkout`, tokenData, {
        headers: {
          Authorization: `Bearer ${this.authenticationService.getToken()}`,
        },
      })
      .then((resp) => {
        window.open(resp.data.links[1].href, '_blank');
      });
  }

  // SOCKET WILL TELL US WHEN THE RIDE IS OVER OR REJECTED SO WE CAN UPDATE THIS

  fetchCurrentRide = async () => {
    await axios
      .get(`/api/passengers/current-ride`, {
        headers: {
          Authorization: `Bearer ${this.authenticationService.getToken()}`,
        },
      })
      .then((res) => {
        if (res.data) this.currentRide = res.data;
      })
      .catch((err) => {
        this.currentRide = null;
      });
  };

  async register(data: any): Promise<any> {
    await axios
      .post(`/api/passengers`, data, {
        headers: {
          Authorization: `Bearer ${this.authenticationService.getToken()}`,
        },
      })
      .then((res) => {
        return res.data;
      });
  }

  getCurrentRide = () => {
    return this.currentRide;
  };

  setCurrentRide = (ride: RideSimple) => {
    this.currentRide = ride;
  };

  getPassengers(
    name: string,
    surname: string,
    username: string,
    page: number
  ): Promise<any> {
    return axios.post(
      `/api/passengers/search`,
      { name: name, surname: surname, username: username, page: page },
      {
        headers: {
          Authorization: `Bearer ${this.authenticationService.getToken()}`,
        },
      }
    );
  }

  getRides(page: number, amount: number, sortBy: string): Promise<any> {
    const username: string = this.authenticationService.getSession()!.username;
    return axios.get(
      `/api/rides/ride-history?username=${username}&page=${page}&amount=${amount}&sortBy=${sortBy}`,
      {
        headers: {
          Authorization: `Bearer ${this.authenticationService.getToken()}`,
        },
      }
    );
  }

  markFavouriteRoute(routeId: number): Promise<boolean> {
    return axios.post(
      `/api/routes/mark-route-as-favourite`,
      { routeId },
      {
        headers: {
          Authorization: `Bearer ${this.authenticationService.getToken()}`,
        },
      }
    );
  }

  unmarkFavouriteRoute(routeId: number): Promise<boolean> {
    return axios.post(
      `/api/routes/unmark-route-as-favourite`,
      { routeId },
      {
        headers: {
          Authorization: `Bearer ${this.authenticationService.getToken()}`,
        },
      }
    );
  }

  getFavouriteRoute(page: number): Promise<any> {
    return axios.get(`/api/routes/favourite-routes?page=${page}&amount=${1}`, {
      headers: {
        Authorization: `Bearer ${this.authenticationService.getToken()}`,
      },
    });
  }
}
