import { Component, OnInit } from '@angular/core';
import { RideReportService } from 'src/app/core/http/ride/rideReportService';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-ride-reports',
  templateUrl: './ride-reports.component.html',
})
export class RideReportsComponent implements OnInit {
  graphData : { name: string; value: number; }[] = [];
  graphLoaded = true;
  xAxisName : string = '';
  yAxisName : string = '';
  activeSelect : string = '';
  
  rideReportForm = new FormGroup({
    startDate: new FormControl('', []),
    endDate: new FormControl('', []),
    type: new FormControl('', []),
  });

  constructor(private rideReportService: RideReportService) {
    
   }

   public async test() : Promise<void>{
    console.log(this.startDate?.value);
    console.log(this.endDate?.value);
    console.log(this.type?.value);
    var data = await this.rideReportService.getReport(this.startDate?.value!,this.endDate?.value!,this.type?.value!);
    var i = 0;
    this.graphData = [];
    for(i = 0; i < data.xaxisValues.length; i++){
      this.graphData.push({ name: data.xaxisValues[i], value: data.yaxisValues[i] });
    }
    this.xAxisName = data.xaxisName;
    this.graphData = [...this.graphData];
    this.yAxisName = data.yaxisName;
    console.log(data);
    this.graphLoaded = false;
    this.graphLoaded = true;
   }

  async ngOnInit(): Promise<void> {
  }

  public changeActiveSelect() : void{
    this.activeSelect = this.type?.value!;
    console.log(this.activeSelect);
  }
  
  get startDate() {
    return this.rideReportForm.get('startDate');
  }
  get endDate() {
    return this.rideReportForm.get('endDate');
  }

  get type() {
    return this.rideReportForm.get('type');
  }


}
