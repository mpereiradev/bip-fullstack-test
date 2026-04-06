import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { BeneficioService } from '../services/beneficio.service';
import { Beneficio } from '../models/beneficio.model';
import { CurrencyMaskDirective } from '../directives/currency-mask.directive';

@Component({
  selector: 'app-transferencia-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CurrencyPipe, CurrencyMaskDirective],
  templateUrl: './transferencia-modal.component.html',
})
export class TransferenciaModalComponent implements OnInit {

  @Input({ required: true }) beneficio!: Beneficio;
  @Input() todosOsBeneficios: Beneficio[] = [];
  @Output() fechar     = new EventEmitter<void>();
  @Output() transferido = new EventEmitter<void>();

  form:     FormGroup;
  destinos  = signal<Beneficio[]>([]);
  enviando  = signal(false);
  erro      = signal<string | null>(null);

  constructor(private fb: FormBuilder, private service: BeneficioService) {
    this.form = this.fb.group({
      toId:  ['', Validators.required],
      valor: [null, [Validators.required, Validators.min(0.01)]]
    });
  }

  ngOnInit(): void {
    this.destinos.set(this.todosOsBeneficios.filter(b => b.id !== this.beneficio.id && b.ativo));
  }

  confirmar(): void {
    if (this.form.invalid) return;
    this.enviando.set(true);
    this.erro.set(null);

    this.service.transferir({
      fromId: this.beneficio.id,
      toId:   Number(this.form.value.toId),
      valor:  this.form.value.valor
    }).subscribe({
      next:  () => this.transferido.emit(),
      error: err => {
        this.erro.set(err?.error?.message ?? 'Erro ao realizar transferência.');
        this.enviando.set(false);
      }
    });
  }

  onBackdrop(e: Event): void {
    if (e.target === e.currentTarget) this.fechar.emit();
  }
}
