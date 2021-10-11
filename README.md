## demo-app
To launch app type in command prompt:

#### docker build -t demo-app github.com/taorusb/demo-app
#### docker run -i demo-app

Controller endpoint:

#### /api/v1/result?currency_code=USD

The parameter is optional. Default value is 'USD' you can also change it in .properties file

#### openexchangerates.default.currency.value

Also, you need to specify values for:

OpenExchangeRates
#### OER_API_KEY 

Giphy
#### GIPHY_API_KEY