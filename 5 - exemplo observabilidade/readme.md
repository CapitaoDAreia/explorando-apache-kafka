# Exemplo de Observabilidade com Kafka

## Como executar exemplo
1. Execute o comando `docker-compose up` na raiz do projeto (use `-d` para rodar em background)

## O que está sendo executado
1. Acesse http://localhost:7071/ para se conectar ao agente JMX
   - O agente JMX (Java Management Extensions) expõe métricas do Kafka, e, nesse caso, está sendo utilizado em conjunto com o **jmx_prometheus_javaagent**, que converte essas métricas para o formato que o Prometheus entende. 
   - Essas métricas incluem informações sobre o desempenho do Kafka, como o número de mensagens processadas, latências, utilização de recursos, entre outras. O conteúdo que você verá nessa porta será uma série de métricas no formato que o Prometheus pode consumir para monitoramento e análise.
2. Acesse http://localhost:3000/ para acessar o Grafana
   - O Grafana permite criar e visualizar dashboards interativos que podem ser alimentados por diversas fontes de dados, como o Prometheus, que também está rodando neste `./docker-compose`.
   - Na primeira vez que acessar o Grafana, você verá a página de login. O nome de usuário e a senha padrão costumam ser admin para ambos, a menos que tenham sido configurados de outra forma.
   - Depois de logar, você poderá configurar fontes de dados (como o Prometheus) e criar dashboards personalizados para monitorar as métricas que estão sendo coletadas (por exemplo, as métricas do Kafka que o Prometheus está coletando na porta `7071`).
3. Acesse http://localhost:9090/ para acessar a interface web do Prometheus
   - **Essa interface permite**:
     - **Visualizar métricas coletadas**: Prometheus coleta métricas de diversas fontes (como o Kafka via JMX no seu caso) e as exibe em sua interface. Você pode pesquisar por métricas e visualizar os valores atuais.
     - **Escrever consultas PromQL**: Prometheus usa sua própria linguagem de consulta chamada PromQL. Você pode usar essa interface para escrever consultas, visualizar gráficos de séries temporais, e analisar dados históricos das métricas.
     - **Configurar regras de alertas**: Caso tenha configurado alertas no Prometheus, você pode gerenciá-los e visualizar alertas ativos.
     - **Status do servidor**: A interface também oferece uma visão do status do próprio servidor Prometheus, incluindo o tempo de scrape, consumo de memória, uso de CPU, entre outras métricas.
     
   Na prática, será o ponto de entrada para consultar as métricas que o Prometheus está coletando (como as do Kafka que estão sendo expostas pelo agente JMX na porta `7071`).