ALTER TABLE template_button
    ADD COLUMN link_mo VARCHAR(500) NULL AFTER ordering,
ADD COLUMN link_type VARCHAR(10) NULL AFTER link_ios;
