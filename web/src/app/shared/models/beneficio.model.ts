export interface Beneficio {
  id: number;
  nome: string;
  descricao: string | null;
  valor: number;
  ativo: boolean;
  version: number;
}

export interface BeneficioRequest {
  nome: string;
  descricao: string | null;
  valor: number;
}

export interface TransferenciaRequest {
  fromId: number;
  toId: number;
  valor: number;
}

export interface ErrorResponse {
  status: number;
  error: string;
  message: string;
  timestamp: string;
}
