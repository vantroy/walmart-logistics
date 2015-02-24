walmart-logistics
=================


## Continuos Integration

Na AWS esta uma  EC2 (t3-small) rodando Jenkins com a CI do projeto, e também servindo o WebService
em uma instacia Tomcat.

### Dados de acesso Jenkins:

Jenkins: [http://52.10.60.30:8090](http://52.10.60.30:8080)
```
user: walmart
pass: giani
```

### Live WebService

Tomcat CXF SOAP service summary: [http://52.10.60.30:8080](http://52.10.60.30:8080)

Endpoint: Endpoint address: [http://52.10.60.30:8080/LogisticsWS](http://52.10.60.30:8080/LogisticsWS)

WSDL: [http://52.10.60.30:8080/LogisticsWS?wsdl](http://52.10.60.30:8080/LogisticsWS)


## Execução:

### Standalone / teste

O projeto vem preparado com um container Jetty embutido no build. Para startar o WebService basta executar da raiz do projeto:

```
    mvn jetty:run
```

### Servidor de aplicação

Depois de executar o build com ```mvn clean install```, copiar a o arquivo WAR p/ a pasta webapps (ou equivalente) do
container desejado. O projeto foi testado com Tomcat 7.

#### Detalhes:

* Arquivo **db.properties** na pasta **main/resource** indica o caminho onde será criada a base de dados. No *linux é preciso garantir que
o diretório configurado possua permissão de escrita p/ o usuário executando o processo*. O db.properties na pasta test/resource
aponta para o target/ portato quaisquer rsquicios da base de testes é eliminada no mvn clean.



