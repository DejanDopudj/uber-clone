import { Injectable } from '@angular/core';
import axios from 'axios';
import { Session } from 'src/app/shared/models/session.model';
import { AuthenticationService } from '../../authentication/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class DriverService {

  constructor(private authenticationService: AuthenticationService) { }

  getDriverByUsername(username: string): Promise<any> {
    return axios.get(`/api/drivers/${username}`);
  }

  getDriverActivity(): boolean {
    const session: Session | null = this.authenticationService.getSession();
    if (session)
      return session.metadata?.active || false;
    return false;
  }

  toggleActivity(): void {
    axios.patch(`/api/drivers/activity`, {},
    {
      headers: {
        Authorization: `Bearer ${this.authenticationService.getToken()}`
      }
    })
    .then((response) => {
      this.authenticationService.fetchSessionFromServer();
    });
  }
}
