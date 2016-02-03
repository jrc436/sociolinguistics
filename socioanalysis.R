library(lme4)
library(LMERConvenienceFunctions)
library(MuMIn)
library(arm)
library(lmerTest)
sociodata <- read.csv(file="/work/research/sociolinguistics/csvfiles/negsocio.csv")
sociodata2 <- read.csv(file="/work/research/sociolinguistics/csvfiles/negsocioIntens.csv")

lev = c("Archived", "Low", "Medium", "High")
newdat <- sociodata
newdat["Intensity"] = ordered(newdat$Intensity, levels=lev) 
pvars <- c("Casualities", "Age", "Refugees", "IDP", "SameYearIDP", "SameYearRefugees", "SameYearFatalities", "Controversy")
newdat[pvars] <- lapply(newdat[pvars],scale)
newdat["Region"] = factor(newdat$Region)

newdat2 = newdat
newdat2["Intensity"] = sociodata2$Intensity
newpvars <- c("Intensity", "Casualities", "Age", "Refugees", "IDP", "SameYearIDP", "SameYearRefugees", "SameYearFatalities", "Controversy")
newdat2[newpvars] <- lapply(newdat2[newpvars],scale)

model_externalities <- lmer(logscore ~ Casualities + IDP + Refugees + (1|Author) + (1|Subreddit), data=newdat)
model_type <- lmer(logscore ~ Foreign + Separatist + (1|Author) + (1|Subreddit), data=newdat)
model_intensity <- lmer(logscore ~ Intensity  + (1|Author) + (1|Subreddit), data=newdat)
model_intensity_num <- lmer(logscore ~ Intensity  + (1|Author) + (1|Subreddit), data=newdat2)
model_same_year <- lmer(logscore ~ SameYearFatalities + SameYearIDP + SameYearRefugees + (1|Author) + (1|Subreddit), data=newdat)
model_age <- lmer(logscore ~ Age + (1|Author) + (1|Subreddit), data=newdat)
model_region <- lmer(logscore ~ Region + (1|Author) + (1|Subreddit), data=newdat)
model_externalities <- glmer(binscore ~ Casualities + IDP + (1|Author) + (1|Subreddit), data=newdat, family=binomial)

model_externalities <- glmer(binscore ~ Refugees  + (1|Author) + (1|Subreddit), family=binomial, data=newdat)
model_type <- glmer(binscore ~ Separatist  + (1|Author) + (1|Subreddit), data=newdat,  family=binomial)
model_intensity <- glmer(binscore ~ Intensity  + (1|Author) + (1|Subreddit), data=newdat,  family=binomial)
#model_intensity_num <- glmer(binscore ~ Intensity  + (1|Author) + (1|Subreddit), data=newdat2,  family=binomial)
model_same_year <- glmer(binscore ~ SameYearFatalities + SameYearIDP + SameYearRefugees + (1|Author) + (1|Subreddit), data=newdat,  family=binomial)
model_age <- glmer(binscore ~ Age + (1|Author) + (1|Subreddit), data=newdat,  family=binomial)
model_region <- glmer(binscore ~ Region + (1|Author) + (1|Subreddit), data=newdat,  family=binomial)
model_combo <- glmer(binscore ~ Age * Intensity + (1|Author) + (1|Subreddit), data=newdat,  family=binomial)


model_contro <- lmer(Controversy ~ Casualities + (1|Author) + (1|Subreddit), data=newdat)
#cutoffs: <-2, <1, 1, >1, >10


sociodata <- read.csv(file="/work/research/sociolinguistics/csvfiles/possocio.csv")

lev = c("Archived", "Low", "Medium", "High")
newdat <- sociodata
newdat["Intensity"] = ordered(newdat$Intensity, levels=lev) 
pvars <- c("Casualities", "Age", "Refugees", "IDP", "SameYearIDP", "SameYearRefugees", "SameYearFatalities", "Controversy")
newdat[pvars] <- lapply(newdat[pvars],scale)
newdat["Region"] = factor(newdat$Region)

model_externalities <- glmer(binscore ~ Casualities + IDP + (1|Author) + (1|Subreddit), data=newdat, family=binomial)
model_type <- glmer(binscore ~ Foreign + Criminal + Terrorism + Ethnic + Separatist + (1|Author) + (1|Subreddit), data=newdat,  family=binomial)
model_intensity <- glmer(binscore ~ Intensity  + (1|Author) + (1|Subreddit), data=newdat,  family=binomial)
model_intensity_num <- glmer(binscore ~ Intensity  + (1|Author) + (1|Subreddit), data=newdat2,  family=binomial)
model_same_year <- glmer(binscore ~ SameYearFatalities + SameYearIDP + SameYearRefugees + (1|Author) + (1|Subreddit), data=newdat,  family=binomial)
model_age <- glmer(binscore ~ Age + (1|Author) + (1|Subreddit), data=newdat,  family=binomial)
model_region <- glmer(binscore ~ Region + (1|Author) + (1|Subreddit), data=newdat,  family=binomial)
