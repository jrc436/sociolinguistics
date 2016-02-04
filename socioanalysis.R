library(lme4)
library(LMERConvenienceFunctions)
library(MuMIn)
library(arm)
library(lmerTest)

#POS
sociodatapos <- read.csv(file="/work/research/sociolinguistics/csvfiles/finalRPos.csv")
lev = c("Archived", "Low", "Medium", "High")
newdatpos <- sociodatapos
newdatpos["Intensity"] = ordered(newdatpos$Intensity, levels=lev) 
pvars <- c("Casualities", "Age", "Refugees", "IDP", "SameYearIDP", "SameYearRefugees", "SameYearFatalities", "Controversy")
newdatpos[pvars] <- lapply(newdatpos[pvars],scale)
newdatpos["Region"] = factor(newdatpos$Region)

posint <- glmer(binscore ~ Intensity  + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)

posfat <- glmer(binscore ~ Casualities  + (1|Author) + (1|Subreddit), family=binomial, data=newdatpos)
posidp <- glmer(binscore ~ IDP  + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)
posref <- glmer(binscore ~ Refugees  + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)

possep <- glmer(binscore ~ Separatist  + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)
poscrim <- glmer(binscore ~ Criminal  + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)
poseth <- glmer(binscore ~ Ethnic  + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)
posterror <- glmer(binscore ~ Terrorism  + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)
posterri <- glmer(binscore ~ Territorial  + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)
posforeign <- glmer(binscore ~ Foreign  + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)

possyfat  <- glmer(binscore ~ SameYearFatalities + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)
possyref <- glmer(binscore ~ SameYearRefugees + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)
possyidp <- glmer(binscore ~ SameYearIDP + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)

posage <- glmer(binscore ~ Age + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)

posregion <- glmer(binscore ~ Region + (1|Author) + (1|Subreddit), data=newdatpos,  family=binomial)

#posfull1 <- glmer(binscore ~ Territorial + Age + (1|Author) + (1|Subreddit), data=newdatneg, family=binomial)
#posfull2 <- glmer(binscore ~ Territorial + Age + Region + (1|Author) + (1|Subreddit), data=newdatneg, family=binomial)
#posfull3 <- glmer(binscore ~ Territorial + Age + Region + Terrorism + (1|Author) + (1|Subreddit), data=newdatneg, family=binomial)

#NEG 

sociodataneg <- read.csv(file="/work/research/sociolinguistics/csvfiles/finalRneg.csv")
newdatneg <- sociodataneg
newdatneg["Intensity"] = ordered(newdatneg$Intensity, levels=lev) 
pvars <- c("Casualities", "Age", "Refugees", "IDP", "SameYearIDP", "SameYearRefugees", "SameYearFatalities", "Controversy")
newdatneg[pvars] <- lapply(newdatneg[pvars],scale)
newdatneg["Region"] = factor(newdatneg$Region)

negint <- glmer(binscore ~ Intensity  + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)

negfat <- glmer(binscore ~ Casualities  + (1|Author) + (1|Subreddit), family=binomial, data=newdatneg)
negidp <- glmer(binscore ~ IDP  + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)
negref <- glmer(binscore ~ Refugees  + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)

negsep <- glmer(binscore ~ Separatist  + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)
negcrim <- glmer(binscore ~ Criminal  + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)
negeth <- glmer(binscore ~ Ethnic  + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)
negterror <- glmer(binscore ~ Terrorism  + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)
negterri <- glmer(binscore ~ Territorial  + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)
negforeign <- glmer(binscore ~ Foreign  + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)

negsyfat  <- glmer(binscore ~ SameYearFatalities + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)
negsyref <- glmer(binscore ~ SameYearRefugees + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)
negsyidp <- glmer(binscore ~ SameYearIDP + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)

negage <- glmer(binscore ~ Age + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)

negregion <- glmer(binscore ~ Region + (1|Author) + (1|Subreddit), data=newdatneg,  family=binomial)

negfull1 <- glmer(binscore ~ Territorial + Age + (1|Author) + (1|Subreddit), data=newdatneg, family=binomial)
negfull2 <- glmer(binscore ~ Territorial + Age + Region + (1|Author) + (1|Subreddit), data=newdatneg, family=binomial)
negfull3 <- glmer(binscore ~ Territorial + Age + Region + Terrorism + (1|Author) + (1|Subreddit), data=newdatneg, family=binomial)