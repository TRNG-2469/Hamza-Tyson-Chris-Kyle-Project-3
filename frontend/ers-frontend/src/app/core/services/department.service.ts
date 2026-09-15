import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';
import { Department } from '../models/department.model';

// department service to get department data from the backend
@Injectable({providedIn: 'root'})
export class DepartmentService {
    // inject HttpClient to make HTTP request ie GET,POST, ETC
    constructor(private http: HttpClient) {

    }
    // get all method hit the GET/departments endpoint
    getAll(): Observable<Department[]> {
        // return the array of departments from the backend using the HttpClient GET
        return this.http.get<Department[]>(`${API_BASE_URL}/departments`);
}}


