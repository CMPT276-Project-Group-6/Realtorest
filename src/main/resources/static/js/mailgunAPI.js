/**
 * So... Kevin is dumb, and don't know how to actually write Java code. So he wrote JavaScript code instead.
 * Hey, as long as it works, right?
 */

require("dotenv").config({path: "etc/secrets/.env"})

const mailgun = require("mailgun.js")
const DOMAIN = "sandboxa1ce28ec9a614494b41d8d52f02c15df.mailgun.org"
const MAILGUN_API_KEY = process.env.MAILGUN_API_KEY
const mg = mailgun({apiKey: MAILGUN_API_KEY, domain: DOMAIN})
const data = {
    from: "Mailgun Sandbox <postmaster@sandboxa1ce28ec9a614494b41d8d52f02c15df.mailgun.org>",
    to: "cmpt276projectgroup6@gmail.com",
    subject: "Hello",
    text: "Testing some Mailgun awesomness!",
}
mg.messages().send(data, function (error, body) {
    // console.log(body)
})

// You can see a record of this email in your logs: https://app.mailgun.com/app/logs.

// You can send up to 300 emails/day from this sandbox server.
// Next, you should add your own domain so you can send 10000 emails/month for free.
