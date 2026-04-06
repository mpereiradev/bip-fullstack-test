import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Beneficio, BeneficioRequest, TransferenciaRequest } from '../models/beneficio.model';

@Injectable({ providedIn: 'root' })
export class BeneficioService {

  private readonly baseUrl = '/api/v1/beneficios';

  constructor(private http: HttpClient) {}

  listar(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.baseUrl);
  }

  buscarPorId(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.baseUrl}/${id}`);
  }

  criar(request: BeneficioRequest): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.baseUrl, request);
  }

  atualizar(id: number, request: BeneficioRequest): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.baseUrl}/${id}`, request);
  }

  desativar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  transferir(request: TransferenciaRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/transfer`, request);
  }
}
