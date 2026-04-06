import { Component, OnInit, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { BeneficioService } from '../shared/services/beneficio.service';
import { Beneficio } from '../shared/models/beneficio.model';
import { TransferenciaModalComponent } from '../shared/transferencia-modal/transferencia-modal.component';

@Component({
  selector: 'app-beneficio-list',
  standalone: true,
  imports: [CommonModule, RouterLink, TransferenciaModalComponent, CurrencyPipe],
  templateUrl: './beneficio-list.component.html',
})
export class BeneficioListComponent implements OnInit {

  beneficios   = signal<Beneficio[]>([]);
  carregando   = signal(true);
  erro         = signal<string | null>(null);
  beneficioSelecionado = signal<Beneficio | null>(null);

  totalBeneficios = computed(() => this.beneficios().length);
  totalAtivos     = computed(() => this.beneficios().filter(b => b.ativo).length);
  totalInativos   = computed(() => this.beneficios().filter(b => !b.ativo).length);
  valorTotal      = computed(() => this.beneficios().reduce((s, b) => s + b.valor, 0));

  private readonly avatarPalette = [
    { bg: '#dce8fb', color: '#1a4fa8' },
    { bg: '#d1fae5', color: '#065f46' },
    { bg: '#fce7f3', color: '#9d174d' },
    { bg: '#fef3c7', color: '#92400e' },
    { bg: '#e0e7ff', color: '#3730a3' },
    { bg: '#fce7f3', color: '#831843' },
  ];

  constructor(private service: BeneficioService) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);
    this.service.listar().subscribe({
      next:  lista => { this.beneficios.set(lista); this.carregando.set(false); },
      error: ()    => { this.erro.set('Erro ao carregar benefícios.'); this.carregando.set(false); }
    });
  }

  excluir(b: Beneficio): void {
    if (!confirm(`Excluir "${b.nome}"?`)) return;
    this.service.desativar(b.id).subscribe({
      next:  () => this.carregar(),
      error: () => this.erro.set('Erro ao excluir benefício.')
    });
  }

  abrirTransferencia(b: Beneficio): void  { this.beneficioSelecionado.set(b); }
  fecharModal(): void                      { this.beneficioSelecionado.set(null); }
  onTransferido(): void                    { this.beneficioSelecionado.set(null); this.carregar(); }

  avatarBg(nome: string): string    { return this.avatarPalette[nome.charCodeAt(0) % this.avatarPalette.length].bg; }
  avatarColor(nome: string): string { return this.avatarPalette[nome.charCodeAt(0) % this.avatarPalette.length].color; }
}
