INSERT INTO categories (id, name, description, custom, user_id) VALUES
    (gen_random_uuid(), 'Alimentação', 'Gastos com alimentação e refeições', false, null),
    (gen_random_uuid(), 'Transporte', 'Gastos com transporte e locomoção', false, null),
    (gen_random_uuid(), 'Moradia', 'Gastos com aluguel, condomínio e manutenção', false, null),
    (gen_random_uuid(), 'Saúde', 'Gastos com saúde e medicamentos', false, null),
    (gen_random_uuid(), 'Educação', 'Gastos com educação e cursos', false, null),
    (gen_random_uuid(), 'Lazer', 'Gastos com entretenimento e lazer', false, null),
    (gen_random_uuid(), 'Salário', 'Receita de salário', false, null),
    (gen_random_uuid(), 'Investimentos', 'Receita de investimentos', false, null),
    (gen_random_uuid(), 'Outros', 'Outros gastos ou receitas', false, null);
