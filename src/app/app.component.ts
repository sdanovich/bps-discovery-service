import { Component, OnInit } from '@angular/core';
import {FileUploader, FileSelectDirective} from 'ng2-file-upload/ng2-file-upload';

const UploadURL = 'http://localhost:9987/bps/directory/v0.1/lookup/suppliers?file='
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'bps-discovery-app';

  public uploader: FileUploader = new FileUploader({url : UploadURL, itemAlias: "agent-discovery-feed"});

  ngOnInit() {
    this.uploader.onAfterAddingFile = (file) => {file.withCredentials = false; };
    this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => 
    alert('File uploaded successfully');
  };
}
