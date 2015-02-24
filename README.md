walmart-logistics
=================

## Sumario

O serviço LogistcsWS é um web service SOAP que expõe 3 métodos, de acordo com o desafio proposto.

* addMap - Adiciona na base de grafos os mapas que serão depois pesquisados

 ** Parametros:

 - String id: o nome único do mapa. Ex: "SP"
 - String map: string contendo a representação de um ponto no mapa, uma conexão e a distancia entre os pontos **por linha**
 Exemplo:
 ```
A B 10
B D 15
A C 20
C D 30
B E 50
D E 30
```



## Integração Contínua

Na AWS esta uma instância EC2 (t3-small) rodando Jenkins com a CI do projeto, e também servindo o WebService
em uma instacia Tomcat.

### Dados de acesso Jenkins:

Jenkins: [http://52.10.60.30:8090](http://52.10.60.30:8080)
```
user: walmart
pass: giani
```

### Live WebService

Tomcat CXF SOAP service summary: [http://52.10.60.30:8080](http://52.10.60.30:8080)

Endpoint: [http://52.10.60.30:8080/LogisticsWS](http://52.10.60.30:8080/LogisticsWS)

WSDL: [http://52.10.60.30:8080/LogisticsWS?wsdl](http://52.10.60.30:8080/LogisticsWS?wsdl)


## Execução:

### Standalone / teste

O projeto vem preparado com um container **Jetty** embutido no build. Para startar o WebService basta executar da raiz do projeto:

```
    mvn jetty:run
```

### Servidor de aplicação

Depois de executar o build com ```mvn clean install```, copiar a o arquivo WAR p/ a pasta webapps (ou equivalente) do
container desejado. O projeto foi testado com Tomcat 7.

### Configuração Importante:

* Arquivo **db.properties** na pasta **main/resource** indica o caminho onde será criada a base de dados. No *linux é preciso garantir que
o diretório configurado possua permissão de escrita p/ o usuário executando o processo*. O db.properties na pasta test/resource
aponta para o target/ portato quaisquer rsquicios da base de testes é eliminada no mvn clean.



