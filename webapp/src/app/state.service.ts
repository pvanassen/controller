import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {catchError, map, reduce} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class StateService {


  constructor(private http: HttpClient) { }

  getState(): Observable<string> {

    return this.http.get("/api/state", { responseType: 'text'})
      .pipe(
        catchError( err => {
          return "error: " + err.message
        })
      )
      .pipe(
        reduce((acc, value) => {
          return acc + value
        })
      )
  }

  shutdown():Observable<any> {
    return this.http.post("/api/state/shutdown", "")
  }

  startup():Observable<any> {
    return this.http.post("/api/state/startup", "")
  }

  shutdownNow():Observable<any> {
    return this.http.post("/api/state/shutdown-now", "")
  }

  fireworks():Observable<any> {
    return this.http.post("/api/state/fireworks", "")
  }
}
