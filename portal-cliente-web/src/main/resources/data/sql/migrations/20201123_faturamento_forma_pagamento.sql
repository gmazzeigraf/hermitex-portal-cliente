ALTER TABLE tb_forma_pagamento ADD COLUMN in_pedido_faturado character(1);
UPDATE tb_forma_pagamento SET in_pedido_faturado = 'T';
ALTER TABLE tb_forma_pagamento ALTER COLUMN in_pedido_faturado SET NOT NULL;