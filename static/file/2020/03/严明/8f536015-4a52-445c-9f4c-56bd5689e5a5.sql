##**************默认规则 注册信息*************************************
##**************规则数据库中执行以下脚本*******************************

INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'uimeta', 'uimetaLoadRule', 30.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'add', 'mddAddRule', 30.00, NULL, '0', NULL, b'1', NULL, 0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'check', 'mddCheckUniqueRule', 30.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'delete', 'mddReferenceCheckRule', 20.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'delete', 'mddDeleteRule', 30.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'detail', 'mddDetailRule', 30.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'query', 'mddQueryRule', 30.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'querytree', 'mddQueryTreeRule', 30.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'refer', 'mddReferDataRule', 30.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'save', 'mddSaveRule', 30.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'save', 'mddRefreshTsRule', 40.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'save', 'mddFillPKRule', 20.00, NULL, '0', NULL, b'1', NULL,  0, NULL);

#20190508 添加 moveprev movenext movefirst movelast 等
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'movefirst', 'mddMoveFirstRule', 30.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'movelast', 'mddMoveLastRule', 30.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'movenext', 'mddMoveNextRule', 30.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'moveprev', 'mddMovePrevRule', 30.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'movefirst', 'mddDetailRule', 40.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'movelast', 'mddDetailRule', 40.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'movenext', 'mddDetailRule', 40.00, NULL, '0', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'moveprev', 'mddDetailRule', 40.00, NULL, '0', NULL, b'1', NULL,  0, NULL);

#20191022 添加 copy 等
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'copy', 'mddCopyRule', 30.00, NULL, '0', NULL, b'1', NULL,  0, NULL);


########################################指定租户###################################

INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'uimeta', 'uimetaLoadRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'add', 'mddAddRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'check', 'mddCheckUniqueRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'delete', 'mddReferenceCheckRule', 20.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'delete', 'mddDeleteRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'detail', 'mddDetailRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'query', 'mddQueryRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'querytree', 'mddQueryTreeRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'refer', 'mddReferDataRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'save', 'mddSaveRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'save', 'mddRefreshTsRule', 40.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'save', 'mddFillPKRule', 20.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);


#20190508 添加 moveprev movenext movefirst movelast 等
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'movefirst', 'mddMoveFirstRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'movelast', 'mddMoveLastRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'movenext', 'mddMoveNextRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'moveprev', 'mddMovePrevRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'movefirst', 'mddDetailRule', 40.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'movelast', 'mddDetailRule', 40.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'movenext', 'mddDetailRule', 40.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'moveprev', 'mddDetailRule', 40.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);

#20191022 添加 copy 等
INSERT INTO `billruleregister` (`billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`,  `isAsyn`, `config`) VALUES ('common', 'copy', 'mddCopyRule', 30.00, NULL, 'czqne4bp', NULL, b'1', NULL,  0, NULL);