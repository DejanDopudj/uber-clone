import { Injectable } from '@angular/core';
import axios from 'axios';
import { Coordinates } from 'src/app/shared/models/coordinates.model';
import { AuthenticationService } from '../../authentication/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class RideReportService {

  constructor(private authenticationService: AuthenticationService) { }

  getReport(startDate: string, endDate: string, reportParameter: string): Promise<any> {
    var params : string;
    params = "startDate=" + startDate + "&endDate=" +endDate+ "&reportParameter=" + reportParameter;
    return axios.get(`http://localhost:8080/api/rides/generate-report-passenger?`+params, {
        headers: {
          Authorization: `Bearer ${this.authenticationService.getToken()}`,
        },
      }).then((res => {
      return res.data;
    }));
  }

}
