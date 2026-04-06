import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { BeneficioService } from './beneficio.service';
import { Beneficio } from '../models/beneficio.model';

describe('BeneficioService', () => {
  let service: BeneficioService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service  = TestBed.inject(BeneficioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  it('listar() deve fazer GET em /api/v1/beneficios', () => {
    const mock: Beneficio[] = [
      { id: 1, nome: 'Beneficio A', descricao: null, valor: 1000, ativo: true, version: 0 },
    ];

    service.listar().subscribe(res => expect(res).toEqual(mock));

    const req = httpMock.expectOne('/api/v1/beneficios');
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('buscarPorId() deve fazer GET em /api/v1/beneficios/:id', () => {
    const mock: Beneficio = { id: 1, nome: 'Beneficio A', descricao: null, valor: 1000, ativo: true, version: 0 };

    service.buscarPorId(1).subscribe(res => expect(res).toEqual(mock));

    const req = httpMock.expectOne('/api/v1/beneficios/1');
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('criar() deve fazer POST em /api/v1/beneficios', () => {
    const payload = { nome: 'Novo', descricao: null, valor: 500 };
    const mock: Beneficio = { id: 2, ...payload, ativo: true, version: 0 };

    service.criar(payload).subscribe(res => expect(res).toEqual(mock));

    const req = httpMock.expectOne('/api/v1/beneficios');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush(mock);
  });

  it('transferir() deve fazer POST em /api/v1/beneficios/transfer', () => {
    const payload = { fromId: 1, toId: 2, valor: 200 };

    service.transferir(payload).subscribe();

    const req = httpMock.expectOne('/api/v1/beneficios/transfer');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush(null);
  });
});
