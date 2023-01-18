import { Injectable } from '@angular/core';
import axios from 'axios';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../../authentication/authentication.service';

@Injectable({
  providedIn: 'root',
})
export class PhotoService {
  constructor(private authenticationService: AuthenticationService) {}

  async storeImage(file: File): Promise<any> {
    const formData = new FormData();
    formData.append('file', file);
    await axios
      .post(`/api/image/save`, formData, {
        headers: {
          Authorization: `Bearer ${this.authenticationService.getToken()}`,
        },
      })
      .then((res) => {
        return res.data;
      });
  }

  loadImage(photoName: string): Promise<any> {
    return axios.get(`/api/image/load/${photoName}`);
  }
}
