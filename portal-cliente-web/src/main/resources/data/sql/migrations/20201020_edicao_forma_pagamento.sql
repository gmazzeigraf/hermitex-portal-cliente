ALTER TABLE tb_forma_pagamento ADD COLUMN in_desconto_especial character(1) NULL;
UPDATE tb_forma_pagamento SET in_desconto_especial = 'N';
ALTER TABLE tb_forma_pagamento ALTER COLUMN in_desconto_especial SET NOT NULL;
